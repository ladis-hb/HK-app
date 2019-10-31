package lads.dev.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import lads.dev.R;


/**
 * Created by Administrator on 2019-07-24
 */
public class NavFragment extends Fragment {

    String TAG = "NavFragment";

    FragmentTransaction transaction;
    FragmentManager fragmentManager;
    List<Fragment> fragments;
    DevHomeFragment devHomeFragment;
    DevUpsFragment devUpsFragment;
    DevAcFragment devAcFragment;
    DevEmFragment devEmFragment;
    DevTHFragment devTHFragment;
    DevIoFragment devIoFragment;
    DevSettingFragment devSettingFragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        fragmentManager = getFragmentManager();
        fragments = new ArrayList<>();

        View view = inflater.inflate(R.layout.nav_frag, container, false);
        RecyclerView navRecyclerView = (RecyclerView) view.findViewById(R.id.nav_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        navRecyclerView.setLayoutManager(layoutManager);
        NavAdapter adapter = new NavAdapter(getNavs());
        navRecyclerView.setAdapter(adapter);
adapter.displayHomeFrag();
        return view;
    }

    private List<String> getNavs() {
        List<String> list = new ArrayList<>();
        list.add("Home");
        list.add("UPS");
        list.add("AC");
        list.add("EM");
        list.add("TH");
        list.add("IO");
        list.add("Setting");
        return list;
    }

    class NavAdapter extends RecyclerView.Adapter<NavAdapter.ViewHolder> {

        List<String> navList;


        class ViewHolder extends RecyclerView.ViewHolder {
            TextView navText;
            public ViewHolder(View view) {
                super(view);
                navText = view.findViewById(R.id.nav_text);
            }
        }

        public NavAdapter(List<String> navList) {
            this.navList = navList;
        }

        @NonNull
        @Override
        public NavAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.nav_item, parent, false);
            final ViewHolder viewHolder = new ViewHolder(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String str = navList.get(viewHolder.getAdapterPosition());
                    if(str.equals("Home")) {
                        Log.d(TAG, "~~~clicked home");
                        if(devHomeFragment == null) {
                            devHomeFragment = new DevHomeFragment();
                            fragments.add(devHomeFragment);
                            hideOtherFragment(devHomeFragment, true);
                        } else {
                            hideOtherFragment(devHomeFragment, false);
                        }
                    } else if(str.contains("UPS")) {
                        if(devUpsFragment == null) {
                            devUpsFragment = new DevUpsFragment();
                            fragments.add(devUpsFragment);
                            hideOtherFragment(devUpsFragment, true);
                        } else {
                            hideOtherFragment(devUpsFragment, false);
                        }
                    } else if(str.contains("AC")) {
                        if(devAcFragment == null) {
                            devAcFragment = new DevAcFragment();
                            fragments.add(devAcFragment);
                            hideOtherFragment(devAcFragment, true);
                        } else {
                            hideOtherFragment(devAcFragment, false);
                        }
                    } else if(str.contains("EM")) {
                        if(devEmFragment == null) {
                            devEmFragment = new DevEmFragment();
                            fragments.add(devEmFragment);
                            hideOtherFragment(devEmFragment, true);
                        } else {
                            hideOtherFragment(devEmFragment, false);
                        }
                    } else if(str.equals("TH")) {
                        Log.d(TAG, "~~~clicked 温湿度");
                        if(devTHFragment == null) {
                            devTHFragment = new DevTHFragment();
                            fragments.add(devTHFragment);
                            hideOtherFragment(devTHFragment, true);
                        } else {
                            hideOtherFragment(devTHFragment, false);
                        }
                    } else if(str.equals("IO")) {
                        if(devIoFragment == null) {
                            devIoFragment = new DevIoFragment();
                            fragments.add(devIoFragment);
                            hideOtherFragment(devIoFragment, true);
                        } else {
                            hideOtherFragment(devIoFragment, false);
                        }
                    } else if(str.equals("Setting")) {
                        Log.d(TAG, "~~~clicked setting");
                        if(devSettingFragment == null) {
                            devSettingFragment = new DevSettingFragment();
                            fragments.add(devSettingFragment);
                            hideOtherFragment(devSettingFragment, true);
                        } else {
                            hideOtherFragment(devSettingFragment, false);
                        }
                    }
                }
            });


            return viewHolder;
        }

        public void displayHomeFrag() {
            devHomeFragment = new DevHomeFragment();
            fragments.add(devHomeFragment);
            hideOtherFragment(devHomeFragment, true);
        }

        @Override
        public int getItemCount() {
            return navList.size();
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.navText.setText(navList.get(position));
        }

        private void hideOtherFragment(Fragment showFragment, boolean add) {
            transaction = fragmentManager.beginTransaction();
            if (add)
                transaction.add(R.id.main_container_content, showFragment);
            for (Fragment fragment : fragments) {
                if (showFragment.equals(fragment)) {
                    transaction.show(fragment);
                } else {
                    transaction.hide(fragment);
                }
            }
            transaction.commit();
        }
    }
}
