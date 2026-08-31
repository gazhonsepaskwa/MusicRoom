#!/bin/bash

# Color codes for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Check for root privileges
if [ "$EUID" -ne 0 ]; then
    echo -e "${RED}Error: Please run as root (use sudo)${NC}"
    exit 1
fi

# Install Certbot based on OS
if ! command -v certbot &> /dev/null; then
    echo "Certbot not found. Installing..."
    
    if [ -f /etc/os-release ]; then
        # Linux Installation (Debian/Ubuntu based)
        . /etc/os-release
        case $ID in
            ubuntu|debian)
                apt-get update
                apt-get install -y certbot
                ;;
            centos|rhel|fedora)
                dnf install -y certbot
                ;;
            *)
                echo -e "${RED}Error: Linux OS detected, but package manager failed.${NC}"
                exit 1
                ;;
        esac
    elif [ "$(uname)" == "Darwin" ]; then
        # macOS Installation (requires Homebrew)
        echo "MacOS detected. Checking for Homebrew..."
        
        if ! command -v brew &> /dev/null; then
            echo -e "${YELLOW}Homebrew not found. Installing Homebrew...${NC}"
            /bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
        fi

        # Check if certbot is installed via brew
        if ! brew list certbot &> /dev/null; then
            echo "Installing Certbot via Homebrew..."
            brew install certbot
        fi
    else
        echo -e "${RED}Error: OS could not be automatically detected.${NC}"
        exit 1
    fi
fi

echo -e "${GREEN}Certbot is installed.${NC}\n"

# Prompt for Inputs
read -p "Enter your Email Address (for Let's Encrypt): " EMAIL
read -p "Enter Domain Name (e.g., example.com): " DOMAIN
read -p "Enter Alias (e.g., www): " ALIAS

# Construct full domain string
if [ -z "$ALIAS" ]; then
    FULL_DOMAIN=$DOMAIN
else
    FULL_DOMAIN="$ALIAS.$DOMAIN"
fi

echo "Requesting Certificate for: $FULL_DOMAIN"

# Run Certbot
# --standalone: Certbot runs a temporary server
# --agree-tos: Accepts terms
# --non-interactive: Prevents hanging for input
# --no-eff-email: Don't share email with EFF (Electronic Frontier Foundation)
# --force-renewal: Updates existing cert if valid for >60 days

sudo certbot certonly \
    --email "$EMAIL" \
    --domains "$FULL_DOMAIN" \
    --agree-tos \
    --non-interactive \
    --no-eff-email \
    --force-renewal

# Check result
if [ $? -eq 0 ]; then
    echo -e "\n${GREEN}Success! Certificate generated at: /etc/letsencrypt/live/$FULL_DOMAIN/cert.pem${NC}"
    echo -e "${GREEN}Private Key: /etc/letsencrypt/live/$FULL_DOMAIN/privkey.pem${NC}"
else
    echo -e "\n${RED}Error: Certificate request failed.${NC}"
fi