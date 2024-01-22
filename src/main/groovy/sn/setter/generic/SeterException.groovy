package sn.setter.generic

class SeterException extends RuntimeException{

    String codeMsg
    String msgToLog
    List<String> args

    public SeterException() {
        super()
    }

    public SeterException(String message) {
        super(message)
    }

    public SeterException(String message, String codeMsg) {
        super(message)
        this.codeMsg = codeMsg
    }

    public SeterException(String codeMsg, String msgToLog, List<String> args) {
        super(msgToLog)
        this.codeMsg = codeMsg
        this.msgToLog = msgToLog
        this.args = args

    }

    public SeterException(String message, String codeMsg, String msgToLog) {
        super(message)
        this.codeMsg = codeMsg
        this.msgToLog = msgToLog
    }

    String getCodeMsg() {
        return codeMsg
    }

    String getMsgToLog() {
        if (msgToLog == null) {
            return this.getMessage()
        } else {
            return this.msgToLog
        }
    }

    public String toString() {
        return (this.codeMsg==null) ? this.getMessage() : this.codeMsg + " - " + this.getMessage()
    }
}
