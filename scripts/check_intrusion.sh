#!/usr/bin/env bash
# ==============================================================================
# AWS Linux 서버 종합 침입 감지 진단 스크립트 (마스킹 적용)
# ==============================================================================

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

print_section() {
    echo -e "\n${BLUE}======================================================================${NC}"
    echo -e "${YELLOW}[+] $1${NC}"
    echo -e "${BLUE}======================================================================${NC}"
}

if [ "$EUID" -ne 0 ]; then
  echo -e "${RED}[!] 경고: 전체 로그 및 루트 디렉토리 점검을 위해 root 권한 실행을 권장합니다.${NC}"
  echo -e "권장 실행법: sudo bash $0\n"
fi

# 1. 접속 세션 및 부팅/로그인 이력 (최근 3개월)
print_section "1. 현재 접속 세션 및 부팅/로그인 이력 점검 (최근 3개월)"
echo -e "${GREEN}--> [현재 접속자 세션 (w)]${NC}"
w

echo -e "\n${GREEN}--> [최근 3개월 이내 로그인 및 부팅 이력 (last -a -s "-90days")]${NC}"
last -a -s "-90days" 2>/dev/null || last -a | head -n 30

# 2. SSH 로그인 성공/실패 로그 (최근 3개월)
print_section "2. SSH 접속 성공 및 실패 로그 점검 (최근 3개월)"
echo -e "${GREEN}--> [최근 3개월 SSH 로그인 성공 기록 (Accepted)]${NC}"
if command -v journalctl &> /dev/null; then
    journalctl -u sshd -u ssh --since "3 months ago" --no-pager 2>/dev/null | grep -i "Accepted" | tail -n 30
fi
if [ -f /var/log/secure ]; then
    grep "Accepted" /var/log/secure | tail -n 30
elif [ -f /var/log/auth.log ]; then
    grep "Accepted" /var/log/auth.log | tail -n 30
fi

echo -e "\n${GREEN}--> [최근 3개월 SSH 로그인 실패/무단 시도 기록 (Failed/Invalid)]${NC}"
if command -v journalctl &> /dev/null; then
    journalctl -u sshd -u ssh --since "3 months ago" --no-pager 2>/dev/null | grep -i -E "Failed|Invalid" | tail -n 30
fi
if [ -f /var/log/secure ]; then
    grep -E "Failed|Invalid" /var/log/secure | tail -n 30
elif [ -f /var/log/auth.log ]; then
    grep -E "Failed|Invalid" /var/log/auth.log | tail -n 30
fi

# 3. 네트워크 활성 연결 및 오픈 포트
print_section "3. 네트워크 세션 및 오픈 포트 점검"
echo -e "${GREEN}--> [외부 연결 중인 아웃바운드/인바운드 세션 (ESTABLISHED)]${NC}"
if command -v ss &> /dev/null; then
    ss -tupn state established
elif command -v netstat &> /dev/null; then
    netstat -antp | grep ESTABLISHED
fi

echo -e "\n${GREEN}--> [현재 열려 있는 수신 포트 (LISTEN)]${NC}"
if command -v ss &> /dev/null; then
    ss -tuln
elif command -v netstat &> /dev/null; then
    netstat -tuln
fi

# 4. 프로세스 및 리소스 이상 점검
print_section "4. CPU / 메모리 고점유 프로세스 TOP 10"
echo -e "${GREEN}--> [CPU 사용량 상위 프로세스 (채굴 봇넷 점검)]${NC}"
ps aux --sort=-%cpu | head -n 11

echo -e "\n${GREEN}--> [메모리 사용량 상위 프로세스]${NC}"
ps aux --sort=-%mem | head -n 11

# 5. 크론탭(Cron) 및 스케줄러 점검
print_section "5. 예약 작업 (Cron) 점검"
echo -e "${GREEN}--> [현재 실행 사용자 crontab]${NC}"
crontab -l 2>/dev/null || echo "등록된 사용자 crontab 없음"

if [ "$EUID" -eq 0 ]; then
    echo -e "\n${GREEN}--> [root 사용자 crontab]${NC}"
    crontab -u root -l 2>/dev/null || echo "등록된 root crontab 없음"
fi

echo -e "\n${GREEN}--> [/etc/cron* 시스템 디렉토리 항목 수록]${NC}"
ls -la /etc/cron* /etc/crontab 2>/dev/null

# 6. SSH authorized_keys 점검 (마스킹 적용)
print_section "6. SSH 등록 공개키 (authorized_keys) 백도어 점검"
for user_home in /root /home/*; do
    if [ -d "$user_home" ]; then
        auth_file="$user_home/.ssh/authorized_keys"
        if [ -f "$auth_file" ]; then
            echo -e "${GREEN}--> [등록 파일]: $auth_file${NC}"
            # 키 데이터 본문의 중간 20자 이상 구간을 *로 숨김 처리
            sed -E 's/([A-Za-z0-9+/]{10})[A-Za-z0-9+/=]{20,}([A-Za-z0-9+/]{10})/\1********************\2/g' "$auth_file"
            echo ""
        fi
    fi
done

print_section "진단이 완료되었습니다."