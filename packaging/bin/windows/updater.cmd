@echo off
:try_update
if exist vdl.jar.new (
    del /f /q vdl.jar
    move /y vdl.jar.new vdl.jar
    timeout /t 1
    goto :try_update
)
