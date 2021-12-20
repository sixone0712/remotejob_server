package jp.co.canon.rss.logmanager.util;

public enum UserRole {
	JOB("ROLE_JOB"),
	CONFIGURE("ROLE_CONFIGURE"),
	RULES("ROLE_RULES"),
	ADDRESS("ROLE_ADDRESS"),
	ACCOUNT("ROLE_ACCOUNT");

	private final String role;

	UserRole(String role) {
		this.role = role;
	}

	public String getRole() {
		return this.role;
	}
}