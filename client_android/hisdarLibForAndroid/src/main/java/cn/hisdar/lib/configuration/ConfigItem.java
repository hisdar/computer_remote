package cn.hisdar.lib.configuration;

public class ConfigItem {

	public String name;
	public String value;
	public String description;
	
	public ConfigItem() {
		this.name = null;
		this.value = null;
		this.description = null;
	}
	
	public ConfigItem(String configName, String configValue) {
		this.name = configName;
		this.value = configValue;
		this.description = null;
	}
	
	public ConfigItem(String configName, int configValue) {
		this.name = configName;
		this.value = Integer.toString(configValue);
		this.description = null;
	}

	public ConfigItem(String configName, boolean configValue) {
		this.name = configName;
		this.value = Boolean.toString(configValue);
		this.description = null;
	}
	
	public ConfigItem(String configName, String configValue, String description) {
		this.name = configName;
		this.value = configValue;
		this.description = description;
	}
	
	public ConfigItem(ConfigItem configItem) {
		this.name = configItem.name;
		this.value = configItem.value;
		this.description = configItem.description;
	}

	public static ConfigItem parseConfigItem(String configItemString) {
		ConfigItem configItem = null;
		
		// parse config name
		int startIndex = configItemString.indexOf('=');
		if (startIndex < 0) {
			System.err.println("Error config item string:" + configItemString);
			return null;
		}
		
		int endIndex = configItemString.indexOf(',');
		if (endIndex < 0) {
			System.err.println("Error config item string:" + configItemString);
			return null;
		}
		
		String configName = configItemString.substring(startIndex + 1, endIndex);
		
		// parse config value
		startIndex = configItemString.indexOf('=', endIndex);
		if (startIndex < 0) {
			System.err.println("Error config item string:" + configItemString);
			return null;
		}
		
		endIndex = configItemString.indexOf(']', startIndex);
		if (endIndex < 0) {
			System.err.println("Error config item string:" + configItemString);
			return null;
		}
		
		String configValue = configItemString.substring(startIndex + 1, endIndex);
		
		configItem = new ConfigItem(configName, configValue);
		
		return configItem;
	}

	public String toString() {
		return "ConfigItem [name=" + name + ", value=" + value + ", description=" + description + "]";
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ConfigItem other = (ConfigItem) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
}
