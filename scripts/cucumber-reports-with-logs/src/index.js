const { AwsClientsWrapper } = require("./libs/AwsClientWrapper");
const { XmlReportParser } = require("./libs/XmlReportParser");
const { HtmlReportGenerator } = require("./libs/HtmlReportGenerator");
const { TestCaseLogExtractor } = require("./libs/TestCaseLogExtractor");

async function main() {

  if(!process.argv[2] ||(process.argv[3] && !process.argv[4])){
    console.log("Error: node src/index.js evn_name profileName*[optional] roleArn*[optional] (*jointly)")
  }
  
  const envName = process.argv[2];
  let profileName = process.argv[3];
  let roleArn = process.argv[4];
  console.log('PARAM{envName: '+ envName +', profileName: '+profileName+', roleArn: '+roleArn);
  
  const inputXmlReportPath = '../../target/surefire-reports/TEST-it.pagopa.pn.cucumber.CucumberB2BTest.xml';
  const inputJsonReportPath = '../../target/cucumber-report.json';

  const outputHtmlReportFolder = '../../target/surefire-reports/';
  const outputHtmlReportName = 'TEST-it.pagopa.pn.cucumber.CucumberB2BTest.html';



  const awsClient = new AwsClientsWrapper( envName, profileName, roleArn); 
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
