package com.skillberg.notes.ui;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.skillberg.notes.R;
import com.skillberg.notes.db.NotesContract;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Адаптер для заметок
 */
public class NotesAdapter extends CursorRecyclerAdapter<NotesAdapter.ViewHolder> {

    private final OnNoteClickListener onNoteClickListener;
    private final OnNoteDeleteClickListener  onNoteDeleteClickListener;

    public NotesAdapter(Cursor cursor, OnNoteClickListener onNoteClickListener, OnNoteDeleteClickListener onNoteDeleteClickListener) {
        super(cursor);

        this.onNoteClickListener = onNoteClickListener;
        this.onNoteDeleteClickListener = onNoteDeleteClickListener;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
        System.out.println("OnBindViewHolder");

        int idColumnIndex = cursor.getColumnIndexOrThrow(NotesContract.Notes._ID);
        long id = cursor.getLong(idColumnIndex);

        viewHolder.itemView.setTag(id);

        int titleColumnIndex = cursor.getColumnIndexOrThrow(NotesContract.Notes.COLUMN_TITLE);
        String title = cursor.getString(titleColumnIndex);

        viewHolder.titleTv.setText(title);

        int colorColumnIndex = cursor.getColumnIndexOrThrow(NotesContract.Notes.COLUMN_COLOR);
        int color_id = cursor.getInt(colorColumnIndex);

        // String[] colorStrings = { "Белый", "Желтый", "Синий", "Зеленный" };
        int color = 0;
        switch (color_id)
        {
            // + +
            // A R G B
            case 0:
                color = 0;
                break;
            case 1: // yellow
                color = 0xFFFFFF00;
                break;
            case 2: // blue
                color = 0xFF0000FF;
                break;
            case 3: // green
                color = 0xFF00FF00;
                break;
        }

        System.out.println("COLOR IS " + String.format("0x%08X", color));
        viewHolder.colorview.setBackgroundColor(color);

        int dateColumnIndex = cursor.getColumnIndexOrThrow(NotesContract.Notes.COLUMN_UPDATED_TS);
        //long updatedTs = cursor.getLong(dateColumnIndex);
        //Date date = new Date(updatedTs);
       String sDate = cursor.getString(dateColumnIndex);
        viewHolder.dateTv.setText(sDate);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        System.out.println("OnCreateViewHolder");

        View view = layoutInflater.inflate(R.layout.view_item_note, parent, false);

        return new ViewHolder(view);
    }

    /**
     * View holder
     */
    class ViewHolder extends RecyclerView.ViewHolder {

        final SimpleDateFormat SDF = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());

        private final TextView titleTv;
        private final TextView dateTv;
        private  final View colorview;

        public ViewHolder(View itemView) {
            super(itemView);

            this.titleTv = itemView.findViewById(R.id.title_tv);
            this.dateTv = itemView.findViewById(R.id.date_tv);
            this.colorview = itemView.findViewById(R.id.item_color);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    long noteId = (Long) v.getTag();

                    onNoteClickListener.onNoteClick(noteId);
                }
            });

            ImageButton button_del = (ImageButton) itemView.findViewById(R.id.button_del);
            button_del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    long noteId = (Long) ((View)((View)view.getParent()).getParent()).getTag();

                    onNoteDeleteClickListener.onNoteDeleteClick(noteId);
                }
            });
        }
    }

    public interface OnNoteDeleteClickListener {
        void onNoteDeleteClick(long noteId);
    }

    /**
     * Слушатель для обработки кликов
     */
    public interface OnNoteClickListener {
        void onNoteClick(long noteId);
    }
}
