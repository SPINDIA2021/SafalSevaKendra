package com.satmatgroup.safalsevakendra.activities_aeps

data class AepsResponseModel(

    val terminalId: String,
    val requestTransactionTime: String,
    val transactionAmount: String,
    val transactionStatus: String,
    val balanceAmount: String,
    val strMiniStatementBalance: String,
    val bankRRN: String,
    val transactionType: String,
    val fpTransactionId: String,
    val merchantTxnId: String,
    val errorCode: String,
    val errorMessage: String,
    val merchantTransactionId: String,
    val bankAccountNumber: String,
    val ifscCode: String,
    val bcName: String,
    val transactionTime: String,
    val agentId: String,
    val issuerBank: String,
    val customerAadhaarNumber: String,
    val customerName: String,
    val stan: String,
    val rrn: String,
    val uidaiAuthCode: String,
    val bcLocation: String,
    val demandSheetId: String,
    val mobileNumber: String,
    val urnId: String,
    val miniStatementStructureModel: String,
    val miniOffusStatementStructureModel: String,
    val miniOffusFlag: String,
    val transactionRemark: String,
    val bankName: String,
    val prospectNumber: String,
    val internalReferenceNumber: String,
    val biTxnType: String,
    val subVillageName: String,
    val userProfileResponseModel: String,
    val hindiErrorMessage: String,
    val loanAccNo: String,
    val responseCode: String,
    val kotakAgentId: String
)
