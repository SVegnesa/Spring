package alation.pentaho.enums;
public enum GroupEnum {
	GROUP_ALATION("Application Settings"),
	GROUP_SERVER("Server Connection"),
	GROUP_LOGGING("Application Logging"),
	GROUP_ADDITIONAL_SETTINGS("Additional Settings");
		public final String groupName;
		public String getGroupName() {
			return groupName;
		}
		private GroupEnum(String groupName) {
			this.groupName = groupName;
		}
}