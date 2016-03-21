/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.project;

import com.eas.client.cache.PlatypusFiles;
import java.io.IOException;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ProjectFactory;
import org.netbeans.spi.project.ProjectState;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Gala
 */
@org.openide.util.lookup.ServiceProvider(service = org.netbeans.spi.project.ProjectFactory.class, position = 839)
public final class PlatypusProjectFactory implements ProjectFactory {

    @Override
    public boolean isProject(FileObject fo) {
        if (!fo.isFolder()) {
            return false;
        }
        FileObject appDirectory = fo.getFileObject("app");
        return appDirectory != null 
                && appDirectory.isFolder()
                && fo.getFileObject(PlatypusProjectSettingsImpl.PROJECT_SETTINGS_FILE) != null;
    }

    @Override
    public Project loadProject(FileObject fo, ProjectState ps) throws IOException {
        try {
            if (isProject(fo)) {
                return new PlatypusProjectImpl(fo, ps);
            } else {
                return null;
            }
        } catch (Exception ex) {
            // no-op
            return null;
        }
    }

    @Override
    public void saveProject(Project aProject) throws IOException, ClassCastException {
        if (aProject instanceof PlatypusProjectImpl) {
            try {
                PlatypusProjectImpl project = (PlatypusProjectImpl) aProject;
                project.save();
            } catch (Exception ex) {
                throw new IOException(ex);
            }
        }
    }
}
