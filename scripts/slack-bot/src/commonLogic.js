const NOTICE_CODE_LENGTH = 18;

function generateRandomValue() {
        let threadNumber = process.pid.toString();
        let numberOfThread = threadNumber.length < 2 ? "0" + threadNumber : threadNumber.substring(0, 2);

        let timeNano = process.hrtime.bigint().toString();

        let randomClassePagamento = Math.floor(Math.random() * 14).toString();
        randomClassePagamento = randomClassePagamento.length < 2 ? "0" + randomClassePagamento : randomClassePagamento;

        let finalNumber = `302${randomClassePagamento}${numberOfThread}${timeNano.substring(0, timeNano.length - 4)}`;

        if (finalNumber.length > NOTICE_CODE_LENGTH) {
            finalNumber = finalNumber.substring(0, NOTICE_CODE_LENGTH);
        } else {
            let remainingLength = NOTICE_CODE_LENGTH - finalNumber.length;
            let paddingString = "";
            for (let i = 0; i < remainingLength; i++) {
                paddingString += Math.floor(Math.random() * 9).toString();
            }
            finalNumber += paddingString;
        }
        return finalNumber;
    }

    module.exports = generateRandomValue;