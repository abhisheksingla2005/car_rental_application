@echo off
echo ===============================================
echo    Car Rental Application - GitHub Updater
echo ===============================================
echo.

REM Check if git is initialized
if not exist ".git" (
    echo Initializing Git repository...
    git init
)

REM Add all changes
echo Adding all changes to Git...
git add .

REM Check if there are any changes to commit
git diff --staged --quiet
if errorlevel 1 (
    echo Changes detected. Creating commit...

    REM Get current date and time for commit message
    for /f "tokens=2 delims==" %%a in ('wmic OS Get localdatetime /value') do set "dt=%%a"
    set "YY=%dt:~2,2%" & set "YYYY=%dt:~0,4%" & set "MM=%dt:~4,2%" & set "DD=%dt:~6,2%"
    set "HH=%dt:~8,2%" & set "Min=%dt:~10,2%" & set "Sec=%dt:~12,2%"
    set "timestamp=%YYYY%-%MM%-%DD% %HH%:%Min%:%Sec%"

    REM Commit with timestamp
    git commit -m "Auto-update: %timestamp%"

    echo.
    echo ===============================================
    echo IMPORTANT: GitHub Repository Setup Required
    echo ===============================================
    echo.
    echo To upload your project to GitHub, you need to:
    echo.
    echo 1. Go to https://github.com and create a new account (if you don't have one)
    echo 2. Create a new repository named 'car-rental-application'
    echo 3. Copy the repository URL from GitHub
    echo 4. Run this command in your terminal:
    echo    git remote add origin [YOUR_GITHUB_REPO_URL]
    echo 5. Run this command to push your code:
    echo    git push -u origin main
    echo.
    echo Example:
    echo    git remote add origin https://github.com/yourusername/car-rental-application.git
    echo    git push -u origin main
    echo.
    echo After the initial setup, just run this batch file whenever you want
    echo to update your GitHub repository with your latest changes!
    echo.
    echo ===============================================

) else (
    echo No changes detected. Repository is up to date.
)

echo.
echo Current Git status:
git status --short

echo.
echo Press any key to exit...
pause >nul
