package com.iamkatrechko.projectmanager.interfce;

import java.util.UUID;

/**
 * @author iamkatrechko
 *         Date: 11.06.2017
 */
public interface OnSubProjectOptionsClick {

    void onDeleteClick(UUID subProjectId, int position);

    void onEditClick(UUID subProjectId, int position);
}
