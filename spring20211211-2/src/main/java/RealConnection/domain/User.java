package RealConnection.domain;

public class User {
	String id;
	int login;
	int recommend;
	Level level;
	String name;
	String password;


	public User(String id, String name, String password,  Level level,int login, int recommend) {
		this.id = id;
		this.login = login;
		this.recommend = recommend;
		this.level = level;
		this.name = name;
		this.password = password;
	}

	public User() {
	}


	public void upgradeLevel() {
		Level nextLevel =this.level.nextLevel(); //현용아 사랑해
		if (nextLevel == null) {
			throw new IllegalStateException("더이상의 레벨은 업그레이드가 불가능합니다." + nextLevel);
		} else {
			this.level=nextLevel;
		}
	}
	public int getLogin() {
		return login;
	}

	public void setLogin(int login) {
		this.login = login;
	}

	public int getRecommend() {
		return recommend;
	}

	public void setRecommend(int recommend) {
		this.recommend = recommend;
	}

	public Level getLevel() {
		return level;
	}

	public void setLevel(Level level) {
		this.level = level;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "User{" +
				"id='" + id + '\'' +
				", login=" + login +
				", recommend=" + recommend +
				", level=" + level +
				", name='" + name + '\'' +
				", password='" + password + '\'' +
				'}';
	}
}
