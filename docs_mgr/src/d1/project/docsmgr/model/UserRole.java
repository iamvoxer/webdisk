package d1.project.docsmgr.model;

import java.util.List;

public class UserRole {
    private String id;
    private String desc;
    private boolean isM;
    private List<UserFolder> roles;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public boolean isM() {
        return isM;
    }

    public void setM(boolean m) {
        isM = m;
    }

    public List<UserFolder> getRoles() {
        return roles;
    }

    public void setRoles(List<UserFolder> roles) {
        this.roles = roles;
    }
}
/*
{
    "id": "developer",
    "desc": "开发",
    "isM": true,
    "roles": [
      {
        "folder":"项目管理",
        "role":"0001",
        "key":"ktyt10YCQ8HInsQrRlRzdJZXWoRRVRY1CRNPmvDY:V-DH8fMczHyCnT5EOAF6RIs3JJVvMNjuBlFyM3gW"
      },
      {
        "folder":"常用软件",
        "role":"1011",
        "key":"ktyt10YCQ8HInsQrRlRzdJZXWoRRVRY1CRNPmvDY:V-DH8fMczHyCnT5EOAF6RIs3JJVvMNjuBlFyM3gW"
      }
    ]
  }
 */