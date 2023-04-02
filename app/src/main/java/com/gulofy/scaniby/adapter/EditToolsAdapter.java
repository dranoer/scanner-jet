package com.gulofy.scaniby.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.gulofy.scaniby.R;
import com.gulofy.scaniby.models.EditToolModel;
import com.gulofy.scaniby.models.EditToolType;

import java.util.ArrayList;

public class EditToolsAdapter extends RecyclerView.Adapter<EditToolsAdapter.ViewHolder> {

    public OnToolSelected onToolSelected;

    public ArrayList<EditToolModel> toolsList = new ArrayList<>();

    public interface OnToolSelected {
        void onToolSelected(EditToolType editToolType);
    }

    public EditToolsAdapter(OnToolSelected onToolSelected2, Context context) {
        onToolSelected = onToolSelected2;
        toolsList.add(new EditToolModel(R.drawable.ic_color_filter, EditToolType.COLORFILTER,context.getString(R.string.color_filter)));
        toolsList.add(new EditToolModel(R.drawable.ic_adjust, EditToolType.ADJUST,context.getString(R.string.adjust)));
        toolsList.add(new EditToolModel(R.drawable.ic_highlight_edit, EditToolType.HIGHLIGHT,context.getString(R.string.high_light)));
        toolsList.add(new EditToolModel(R.drawable.ic_picture, EditToolType.PICTURE,context.getString(R.string.pic)));
        toolsList.add(new EditToolModel(R.drawable.ic_sign, EditToolType.SIGNATURE,context.getString(R.string.signature)));
        toolsList.add(new EditToolModel(R.drawable.ic_watermark, EditToolType.WATERMARK,context.getString(R.string.watermark)));
        toolsList.add(new EditToolModel(R.drawable.ic_text, EditToolType.TEXT,context.getString(R.string.text)));
        toolsList.add(new EditToolModel(R.drawable.ic_overlay, EditToolType.OVERLAY,context.getString(R.string.overlay)));
        toolsList.add(new EditToolModel(R.drawable.ic_color_effect, EditToolType.COLOREFFECT,context.getString(R.string.color_effect)));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.edit_tools_list_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int i) {
        viewHolder.iv_toolIcon.setImageResource(toolsList.get(i).getTool_icon());
        viewHolder.txtIconName.setText(toolsList.get(i).getIcon_name());
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                onToolSelected.onToolSelected(((EditToolModel) toolsList.get(i)).getEditToolType());
            }
        });
    }

    @Override
    public int getItemCount() {
        return toolsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_toolIcon;
        TextView txtIconName;

        public ViewHolder(View view) {
            super(view);
            iv_toolIcon = (ImageView) view.findViewById(R.id.iv_toolIcon);
            txtIconName = (TextView) view.findViewById(R.id.txtIconName);
        }
    }
}
