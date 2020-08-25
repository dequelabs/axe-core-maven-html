package com.deque.html.axecore.results;

public class TestResults {
    private String type;
    private String name;
    private String id;
    private Platform platform = new Platform();
    private TestSubject testSubject = new TestSubject();
    private Results findings;
    private String endTime;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Platform getPlatform() {
        return platform;
    }

    public void setPlatform(Platform platform) {
        this.platform = platform;
    }

    public TestSubject getTestSubject() {
        return testSubject;
    }

    public void setTestSubject(TestSubject testSubject) {
        this.testSubject = testSubject;
    }

    public Results getFindings() {
        return findings;
    }

    public void setFindings(Results findings) {
        this.findings = findings;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TestResults)) return false;

        TestResults that = (TestResults) o;

        if (getType() != null ? !getType().equals(that.getType()) : that.getType() != null) return false;
        if (getName() != null ? !getName().equals(that.getName()) : that.getName() != null) return false;
        if (getId() != null ? !getId().equals(that.getId()) : that.getId() != null) return false;
        if (getPlatform() != null ? !getPlatform().equals(that.getPlatform()) : that.getPlatform() != null)
            return false;
        if (getTestSubject() != null ? !getTestSubject().equals(that.getTestSubject()) : that.getTestSubject() != null)
            return false;
        if (getFindings() != null ? !getFindings().equals(that.getFindings()) : that.getFindings() != null)
            return false;
        return getEndTime() != null ? getEndTime().equals(that.getEndTime()) : that.getEndTime() == null;

    }

    @Override
    public int hashCode() {
        int result = getType() != null ? getType().hashCode() : 0;
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (getId() != null ? getId().hashCode() : 0);
        result = 31 * result + (getPlatform() != null ? getPlatform().hashCode() : 0);
        result = 31 * result + (getTestSubject() != null ? getTestSubject().hashCode() : 0);
        result = 31 * result + (getFindings() != null ? getFindings().hashCode() : 0);
        result = 31 * result + (getEndTime() != null ? getEndTime().hashCode() : 0);
        return result;
    }
}
