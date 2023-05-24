const { AwsClientsWrapper } = require("./libs/AwsClientWrapper");
const { XmlReportParser } = require("./libs/XmlReportParser");
const { HtmlReportGenerator } = require("./libs/HtmlReportGenerator");
const { TestCaseLogExtractor } = require("./libs/TestCaseLogExtractor");

async function main() {
  const envName = 'dev';
  
  const inputXmlReportPath = '../../target/surefire-reports/TEST-it.pagopa.pn.cucumber.CucumberB2BTest.xml';
  const inputJsonReportPath = '../../target/cucumber-report.json';

  const outputHtmlReportFolder = '../../target/surefire-reports/';
  const outputHtmlReportName = 'TEST-it.pagopa.pn.cucumber.CucumberB2BTest.html';



  const awsClient = new AwsClientsWrapper( envName ); // FIXME parametrizzare profilo e regione
  const logExtractor = new TestCaseLogExtractor( awsClient );
  const xmlReportParser = new XmlReportParser();
  const htmlReportGenerator = new HtmlReportGenerator();
  
  try {
    await awsClient.init();

    const testCases = xmlReportParser.parse( inputXmlReportPath );
    const testCasesIds = testCases.listTestCasesIds();
    console.log( "Loaded TestCases: ", testCasesIds );
  
    await htmlReportGenerator.generateReport( 
        inputJsonReportPath, 
        outputHtmlReportFolder + outputHtmlReportName, 
        envName, 
        testCasesIds
      );

    
    const logsByTestCaseAndCall = await logExtractor.extractHttpCallLogs( testCases, console );
    
    await htmlReportGenerator.generateTestCasesLogsReports( 
        outputHtmlReportFolder, 
        logsByTestCaseAndCall 
      );
  }
  catch( error ) {
    console.error( error );
  }

}

main();
