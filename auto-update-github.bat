@echo off
echo Starting automatic Git update...
git add .
git status
set /p commit_message="Enter commit message (or press Enter for default): "
if "%commit_message%"=="" set commit_message=Auto-update: %date% %time%
git commit -m "%commit_message%"
git push
echo Changes pushed to GitHub successfully!
pause

