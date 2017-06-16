package cn.ucai.mifareultralightc.local;

import android.content.Context;

import java.util.List;
import java.util.Map;

/**
 * Created by clawpo on 2017/6/16.
 */

public class ProjectHelper {
    private static final String TAG = "ProjectHelper";
    private Map<Integer,Project> projectMap;
    private List<Project> projectList;
    private ProjectDao projectDao;
    private static ProjectHelper instance = null;

    public synchronized static ProjectHelper getInstance(){
        if (instance==null){
            instance = new ProjectHelper();
        }
        return instance;
    }

    public ProjectHelper() {
    }

    public void init(Context context){
        projectDao = new ProjectDao(context);
    }

    public int saveProjectInfo(String name){
        Project project = projectDao.getProjectByName(name);
        if (project!=null){
            return project.getId();
        }else{
            Project pro = new Project(name);
            return (int) projectDao.saveProject(pro);
        }
    }
}
