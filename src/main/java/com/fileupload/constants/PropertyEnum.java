package com.fileupload.constants;

public enum PropertyEnum {
	SEED_NODE("seed_node"),
	OWN_SERVER_IP("own_server_ip"),
	OWN_SERVER_PORT("own_server_port");
	
	private String name;
	
	private PropertyEnum(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
}
