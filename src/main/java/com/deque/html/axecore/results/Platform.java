package com.deque.html.axecore.results;

public class Platform {
    private String userAgent;
    private String testMachine;

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getTestMachine() {
        return testMachine;
    }

    public void setTestMachine(String testMachine) {
        this.testMachine = testMachine;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Platform)) return false;

        Platform platform = (Platform) o;

        if (getUserAgent() != null ? !getUserAgent().equals(platform.getUserAgent()) : platform.getUserAgent() != null)
            return false;
        return getTestMachine() != null ? getTestMachine().equals(platform.getTestMachine()) : platform.getTestMachine() == null;

    }

    @Override
    public int hashCode() {
        int result = getUserAgent() != null ? getUserAgent().hashCode() : 0;
        result = 31 * result + (getTestMachine() != null ? getTestMachine().hashCode() : 0);
        return result;
    }
}
