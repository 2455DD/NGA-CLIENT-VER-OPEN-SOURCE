package gov.anzong.meizi;


public class MeiziCategory {

    public static final MeiziCategoryItem[] ITEMS = new MeiziCategory.MeiziCategoryItem[] {
        new MeiziCategoryItem("ROSIд��", 2),
            new MeiziCategoryItem("�Ȱ�����", 1),
    };

    public static int getCount(){
    	return ITEMS.length;
    }
    
    public static class MeiziCategoryItem {

        public MeiziCategoryItem(String name, int id) {
            mName = name;
            mID = id;
        }

        public String getName() {
            return mName;
        }

        public int getID() {
            return mID;
        }

        private String mName;

        private int mID;
    }
}
