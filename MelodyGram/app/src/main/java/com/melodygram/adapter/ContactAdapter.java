package com.melodygram.adapter;

import android.app.Activity;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.melodygram.R;
import com.melodygram.chatinterface.InterfaceContact;
import com.melodygram.database.AppContactDataSource;
import com.melodygram.emojicon.EmojiconEditText;
import com.melodygram.emojicon.EmojiconTextView;
import com.melodygram.model.ContactsModel;
import com.melodygram.singleton.AppController;
import com.melodygram.view.SideBarView;


import java.util.ArrayList;
import java.util.Locale;


public class ContactAdapter extends BaseAdapter implements SectionIndexer, Filterable {

    private Activity activityRef;
    private ArrayList<ContactsModel> contactsArrayList;
    private AppController appController;
    private ArrayList<ContactsModel> fixedContactsArrayList;
    private FilterName filterName;
    AppContactDataSource appContactDataSource;
    InterfaceContact contactInstance;

    public ContactAdapter(Activity activityRef,
                          ArrayList<ContactsModel> contactsArrayList,InterfaceContact contactInstance) {
        this.activityRef = activityRef;
        this.contactsArrayList = contactsArrayList;
        appController = AppController.getInstance();
        fixedContactsArrayList = new ArrayList<>();
        fixedContactsArrayList.addAll(contactsArrayList);
        appContactDataSource = new AppContactDataSource(activityRef);
        this.contactInstance = contactInstance;
    }

    @Override
    public int getCount() {
        return contactsArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return contactsArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return contactsArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolderItem viewHolder;
        View listRow = convertView;
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) activityRef
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            listRow = mInflater.inflate(R.layout.contact_list_item, null);
            viewHolder = new ViewHolderItem();
            viewHolder.contactName = (EmojiconTextView) listRow
                    .findViewById(R.id.appUserNameId);
            viewHolder.contactNumber = (TextView) listRow
                    .findViewById(R.id.contactNumberId);
            viewHolder.overlayText = (TextView) listRow
                    .findViewById(R.id.overlay_text);
            viewHolder.contactPic = (ImageView) listRow
                    .findViewById(R.id.contactPicId);
            viewHolder.overLayParent = (RelativeLayout) listRow
                    .findViewById(R.id.overlay_parent);
            listRow.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolderItem) listRow.getTag();
        }

        viewHolder.contactPic.setImageResource(R.drawable.default_profile_pic);
        if (!contactsArrayList.get(position).getContactType()
                .equalsIgnoreCase("phone")) {
            String picUrl = contactsArrayList.get(position)
                    .getAppContactsProfilePic();
            if (!contactsArrayList.get(position).getProfilePicPrivacy()
                    .equalsIgnoreCase("1")
                    && !picUrl.equalsIgnoreCase("")) {
                appController.displayUrlImage(viewHolder.contactPic, picUrl, null);
            } else {
                viewHolder.contactPic.setImageResource(R.drawable.default_profile_pic);
            }

            String phoneName = contactsArrayList.get(position).getPhContactName();
            if (phoneName.length() > 0 && position != 0) {
                char zeroChar = phoneName.charAt(0);
                String lastName = contactsArrayList.get(position - 1).getPhContactName();
                if (lastName.length() > 0) {
                    char zeroCharLat = lastName.charAt(0);
                    if(Character.toUpperCase(zeroChar) !=Character.toUpperCase(zeroCharLat))
                    {
                        viewHolder.overlayText.setText(Character.toUpperCase(zeroChar)+"");
                        viewHolder.overLayParent.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        viewHolder.overLayParent.setVisibility(View.GONE);
                    }
                }
            } else {
                viewHolder.overlayText.setText(Character.toUpperCase(phoneName.charAt(0))+"");
                viewHolder.overLayParent.setVisibility(View.VISIBLE);
            }

            viewHolder.contactName.setText(phoneName);
            if (contactsArrayList.get(position).getStatusPrivacy()
                    .equalsIgnoreCase("0")) {
                viewHolder.contactNumber.setText(contactsArrayList.get(position).getPhChatContactStatus());
            } else  if (contactsArrayList.get(position).getStatusPrivacy()
                    .equalsIgnoreCase("2"))  {
                appContactDataSource.open();
                if(appContactDataSource.isContactAvailableOrNot(contactsArrayList.get(position).getAppContactNumber()))
                {
                    viewHolder.contactNumber.setText(contactsArrayList.get(position).getPhChatContactStatus());
                }else
                {
                    viewHolder.contactNumber.setText(contactsArrayList.get(position).getPhContactNumber());
                }
                appContactDataSource.close();
            }else
            {
                viewHolder.contactNumber.setText(contactsArrayList.get(position).getPhContactNumber());
            }
        } else {
            viewHolder.contactPic.setImageResource(R.drawable.default_profile_pic);
            viewHolder.contactNumber.setText(contactsArrayList.get(position).getPhContactNumber());
        }

        viewHolder.contactPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContactsModel contactsModel = contactsArrayList.get(position);
                contactInstance.response(contactsArrayList.get(position).getPhContactName(),contactsArrayList.get(position).getAppContactsUserId(),position);

            }
        });

        return listRow;
    }

    String mSectionName = "";

    @Override
    public int getPositionForSection(int section) {
        for (int i = 0; i < contactsArrayList.size(); i++) {
            mSectionName = contactsArrayList.get(i).getPhContactName();
            if (!mSectionName.equalsIgnoreCase(" ") && mSectionName != null && mSectionName.length() != 0) {
                final char firstChar = mSectionName.toUpperCase().charAt(0);
                if (firstChar == section) {
                    return i;
                }
            }
        }
        return -1;
    }

    @Override
    public int getSectionForPosition(int arg0) {
        return 0;
    }

    @Override
    public Filter getFilter() {
        if (filterName == null) {
            filterName = new FilterName();
        }
        return filterName;
    }

    class ViewHolderItem {
        TextView contactNumber, overlayText;
        ImageView contactPic;
        EmojiconTextView contactName;
        RelativeLayout overLayParent;
    }

    @Override
    public Object[] getSections() {
        String[] chars = new String[SideBarView.mLetter.length];
        for (int i = 0; i < SideBarView.mLetter.length; i++) {
            chars[i] = String.valueOf(SideBarView.mLetter[i]);
        }
        return chars;
    }

    private class FilterName extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            ArrayList<ContactsModel> list = new ArrayList<>();
            for (int i = 0; i < fixedContactsArrayList.size(); i++) {
                if (fixedContactsArrayList.get(i).getPhContactName()
                        .toLowerCase(Locale.getDefault()).contains(constraint)) {
                    list.add(fixedContactsArrayList.get(i));
                }
            }
            results.count = list.size();
            results.values = list;
            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            contactsArrayList = (ArrayList<ContactsModel>) results.values;
            notifyDataSetChanged();
        }
    }
}
