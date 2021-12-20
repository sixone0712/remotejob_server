package jp.co.canon.rss.logmanager.exception;

public class UnauthorizedException extends RuntimeException{
    private static final long serialVersionUID = -2238030302650813813L;

    public UnauthorizedException() {
        super("Token is invalid.");
    }
}