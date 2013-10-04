package org.jhighfun.util;

final class ExecutionThrottler {
    private String identity;

    public ExecutionThrottler(String identity) {
        this.identity = identity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExecutionThrottler)) return false;

        ExecutionThrottler that = (ExecutionThrottler) o;

        if (identity != null ? !identity.equals(that.identity) : that.identity != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return identity != null ? identity.hashCode() : 0;
    }
}
