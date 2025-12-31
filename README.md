# Playwright + Java Framework

## Quick Start
```bash
mvn clean install
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install"
mvn test
```

## Run Tests
- All: `mvn test`
- Smoke: `mvn test -DsuiteXmlFile=src/test/resources/testng-smoke.xml`
- Headed: `mvn test -Dheadless=false`

## Tests
6 test cases covering login functionality

**By: Pravin - AGS Technology Consulting**
