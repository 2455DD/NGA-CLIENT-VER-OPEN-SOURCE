package gov.anzong.androidnga.activity;

import net.simonvt.menudrawer.MenuDrawer;
import net.simonvt.menudrawer.Position;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import gov.anzong.androidnga.R;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseListSample extends FragmentActivity implements MenuAdapter.MenuListener {

    private static final String STATE_ACTIVE_POSITION =
            "net.simonvt.menudrawer.samples.LeftDrawerSample.activePosition";

    protected MenuDrawer mMenuDrawer;

    protected MenuAdapter mAdapter;
    protected ListView mList;

    private int mActivePosition = 0;
    
    List<Object> items = new ArrayList<Object>();

    @Override
    protected void onCreate(Bundle inState) {
        super.onCreate(inState);

        if (inState != null) {
            mActivePosition = inState.getInt(STATE_ACTIVE_POSITION);
        }

        mMenuDrawer = MenuDrawer.attach(this, MenuDrawer.Type.BEHIND, getDrawerPosition(), getDragMode());

        items.add(new Category("����Ƭ��"));
        items.add(new Item("��½�˺�", R.drawable.ic_login));
        items.add(new Item("yoooo", R.drawable.ic_menu_mylocation));
        items.add(new Item("ǩ������", R.drawable.ic_action_go_to_today));
        //items.add(new Item("�������", R.drawable.ic_action_select_all_dark));
        items.add(new Category("������̳"));
        items.add(new Item("�ۺ�����", R.drawable.ic_action_select_all_dark));
        items.add(new Item("������ϵ��", R.drawable.ic_action_select_all_dark));
        items.add(new Item("ְҵ������", R.drawable.ic_action_select_all_dark));
        items.add(new Item("ð���ĵ�", R.drawable.ic_action_select_all_dark));
        items.add(new Item("�����֮��", R.drawable.ic_action_select_all_dark));
        items.add(new Item("ϵͳ��Ӳ������", R.drawable.ic_action_select_all_dark));
        items.add(new Item("������Ϸ", R.drawable.ic_action_select_all_dark));
        items.add(new Item("�����ƻ���", R.drawable.ic_action_select_all_dark));
        items.add(new Item("¯ʯ��˵", R.drawable.ic_action_select_all_dark));
        items.add(new Item("Ӣ������", R.drawable.ic_action_select_all_dark));
        items.add(new Item("���˰���", R.drawable.ic_action_select_all_dark));
        items.add(new Category("����"));
        items.add(new Item("��������", R.drawable.action_settings));
        items.add(new Item("��Ӱ���", R.drawable.ic_action_add_to_queue));
        items.add(new Item("����������", R.drawable.ic_action_warning));
        items.add(new Item("����", R.drawable.ic_action_about));

        mList = new ListView(this);

        mAdapter = new MenuAdapter(this, items);
        mAdapter.setListener(this);
        mAdapter.setActivePosition(mActivePosition);
        
        //cacheColorHint
        mList.setCacheColorHint(0x00000000);
        mList.setAdapter(mAdapter);
        mList.setOnItemClickListener(mItemClickListener);

        mMenuDrawer.setMenuView(mList);
    }
    
    public void setLocItem(int loc, String itemname){
    	//set item on loc position
    	items.add(loc,new Item(itemname, R.drawable.ic_action_select_all_dark));
    	//reset menu
    	mAdapter = new MenuAdapter(this, items);
    	mList.setCacheColorHint(0x00000000);
    	mList.setAdapter(mAdapter);
    	mMenuDrawer.setMenuView(mList);
    }

    protected abstract void onMenuItemClicked(int position, Item item);

    protected abstract int getDragMode();

    protected abstract Position getDrawerPosition();

    private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mActivePosition = position;
            mMenuDrawer.setActiveView(view, position);
            mAdapter.setActivePosition(position);
            onMenuItemClicked(position, (Item) mAdapter.getItem(position));
        }
    };

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_ACTIVE_POSITION, mActivePosition);
    }

    @Override
    public void onActiveViewChanged(View v) {
        mMenuDrawer.setActiveView(v, mActivePosition);
    }
}
