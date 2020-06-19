package com.deque.html.axecore.results;

public class TestSubject {
    private String fileName;
    private String state;
    private String lineNum;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getLineNum() {
        return lineNum;
    }

    public void setLineNum(String lineNum) {
        this.lineNum = lineNum;
    }

    public String getFormattedLineNum() {
        if (null != getLineNum()) {
            return "Line number: " + getLineNum();
        } else {
            return null;
        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TestSubject)) return false;

        TestSubject that = (TestSubject) o;

        if (getFileName() != null ? !getFileName().equals(that.getFileName()) : that.getFileName() != null)
            return false;
        if (getState() != null ? !getState().equals(that.getState()) : that.getState() != null) return false;
        return getLineNum() != null ? getLineNum().equals(that.getLineNum()) : that.getLineNum() == null;

    }

    @Override
    public int hashCode() {
        int result = getFileName() != null ? getFileName().hashCode() : 0;
        result = 31 * result + (getState() != null ? getState().hashCode() : 0);
        result = 31 * result + (getLineNum() != null ? getLineNum().hashCode() : 0);
        return result;
    }
}
