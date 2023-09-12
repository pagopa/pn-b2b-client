const fs = require('fs');
const { fromIni } = require("@aws-sdk/credential-provider-ini");
const { STSClient, AssumeRoleCommand } = require("@aws-sdk/client-sts");
const { LambdaClient, InvokeCommand } = require("@aws-sdk/client-lambda");


function awsClientCfg( envName, profileName, roleArn ) {
  if(!profileName){
    return { 
      region: "eu-south-1", 
      credentials: fromIni({ 
        profile: `sso_pn-core-${envName}`,
      })
    }
  }else{
    return { 
      region: "eu-south-1", 
      credentials: fromIni({ 
        profile: profileName,
        roleAssumer: async (sourceCredentials, params) => {
          const stsClient = new STSClient({ credentials: sourceCredentials });
          const command = new AssumeRoleCommand({
            RoleArn: roleArn,
            RoleSessionName: "session1"
          });
          const response = await stsClient.send(command);
          return {
            accessKeyId: response.Credentials.AccessKeyId,
            secretAccessKey: response.Credentials.SecretAccessKey,
            sessionToken: response.Credentials.SessionToken,
            expiration: response.Credentials.Expiration
          };
        }
      })
    }
  }
}

class LambdaInvoker {
  constructor(envName, profileName, roleArn) {
    this.lambdaClient = new LambdaClient(awsClientCfg( envName, profileName, roleArn ));
  }

  async invokeLambda(functionName, payload) {
    try {
      const params = {
        FunctionName: functionName,
        Payload: JSON.stringify(payload),
      };

      const command = new InvokeCommand(params);
      const data = await this.lambdaClient.send(command);

      return JSON.parse(new TextDecoder().decode(data.Payload));
    } catch (error) {
      console.error('Errore di invocazione invokeLambda:', error);
      throw error;
    }
  }
}



function writeFile(fileName,contentTowrite,isAppendFunction){
  if(isAppendFunction){
    fs.appendFileSync(fileName,contentTowrite , (err) => {
      if (err) {
        console.error('Si è verificato un errore durante la scrittura del file:', err);
      } else {
        console.log('Il file è stato scritto correttamente!');
      }
    }) 
  }else{
    fs.writeFileSync(fileName, contentTowrite, (err) => {
      if (err) {
        console.error('Si è verificato un errore durante la scrittura del file:', err);
      } else {
        console.log('Il file è stato scritto correttamente!');
      }
    }) 
  }
}

async function executeLambda(lambdaInvoker, lambdaName, payload){
  const response = await lambdaInvoker.invokeLambda(lambdaName, payload)
  if(response.statusCode === 200){
    let result = JSON.stringify(JSON.parse(response.body)[0]);
    console.log('result: '+result);
    return result;

  }else{
    console.log('Lambda response with status code:', response.statusCode);
  }
}


async function main() {

  if(!process.argv[2] || !process.argv[3] ||(process.argv[4] && !process.argv[5])){
    console.log("Error: node src/index.js evn_name lambdaName profileName*[optional] roleArn*[optional] (*jointly)")
  }

  const envName = process.argv[2];
  let lambdaName = process.argv[3];
  let profileName = process.argv[4];
  let roleArn = process.argv[5];
  console.log('PARAM{envName: '+ envName +', profileName: '+profileName+', roleArn: '+roleArn);

  const fileContent = fs.readFileSync('codiciFiscaliGenerati.txt', 'utf8');
  //console.log('FILE-CONTENT: '+fileContent);
  const payload = JSON.parse(fileContent);
  //console.log('payload: '+JSON.stringify(payload));

  const lambdaInvoker = new LambdaInvoker(envName, profileName, roleArn);


  writeFile('token.json','[ \n',false);
  
  for(let i= 0; i < payload.entries.length; i++ ){
    let newPayload;
    if(payload.entries[i].tokenType.toLowerCase() !== 'pa'){
        newPayload = { entries: [{taxId: payload.entries[i].taxId, tokenType:payload.entries[i].tokenType}]}
    }else{
        newPayload = { entries: [{taxId: payload.entries[i].taxId, tokenType:payload.entries[i].tokenType, paName:payload.entries[i].paName}]}
    }
    console.log('newPayload: '+JSON.stringify(newPayload));
    let result = await executeLambda(lambdaInvoker, lambdaName, newPayload);
    console.log('INTERNA-RESULT: '+result);
    writeFile('token.json', result + (i === (payload.entries.length -1) ? '\n' : ', \n'), true);
  }
 
  writeFile('token.json', '] \n', true);
 
}



main();