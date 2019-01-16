package KittyEngine.Utilities;

import android.content.Context;

import KittyEngine.Container.KArrayList;

public class KFile {

    /**
     * get all files with extensions in the assets directory
     * @param dir starting from this directory.
     *            first directory must be located in assets/.
     *            empty string if starting in assets
     * @param files must be initialized
     */
    public static void getFilesInAssetDir(Context context, String dir, KArrayList<String> files) {
        try {
            String[] paths = context.getAssets().list(dir);

            if (paths == null) {
                return;
            }

            for (int i = 0; i < paths.length; ++i) {
                String path = dir + "/" + paths[i];

                if (path.contains(".")) {
                    files.add(path);
                }
                else {
                    getFilesInAssetDir(context, path, files);
                }
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }
}
