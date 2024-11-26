package com.zhuangfei.android_timetableview.views;
import com.zhuangfei.android_timetableview.R;

import android.content.ClipData;
import android.content.ClipDescription;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.gridlayout.widget.GridLayout;

import java.util.HashMap;
import java.util.Map;

public class EffiSchedulerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_effi_scheduler);

        TextView task1 = findViewById(R.id.task1);
        TextView task2 = findViewById(R.id.task2);

        LinearLayout urgentImportant = findViewById(R.id.urgent_important);
        LinearLayout notUrgentImportant = findViewById(R.id.not_urgent_important);
        LinearLayout urgentNotImportant = findViewById(R.id.urgent_not_important);
        LinearLayout notUrgentNotImportant = findViewById(R.id.not_urgent_not_important);

        // 为任务设置长按监听器，启动拖拽
        task1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return startDrag(view);
            }
        });

        task2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return startDrag(view);
            }
        });

        // 为象限设置拖拽监听器
        View.OnDragListener dragListener = new View.OnDragListener() {
            @Override
            public boolean onDrag(View view, DragEvent event) {
                return onDragListener(view, event);
            }
        };

        urgentImportant.setOnDragListener(dragListener);
        notUrgentImportant.setOnDragListener(dragListener);
        urgentNotImportant.setOnDragListener(dragListener);
        notUrgentNotImportant.setOnDragListener(dragListener);
    }

    // 开始拖拽
    private boolean startDrag(View view) {
        ClipData.Item item = new ClipData.Item((CharSequence) view.getTag());
        ClipData dragData = new ClipData(
                (CharSequence) view.getTag(),
                new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN},
                item
        );
        View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
        view.startDragAndDrop(dragData, shadowBuilder, view, 0);
        return true;
    }

// A map to store the original background color of each view that is involved in the drag process.
// This allows us to restore the original color once the drag event is complete.
    private final Map<View, Integer> originalBackgroundColors = new HashMap<>();

// onDragListener handles the various drag events that can occur on a view.
// It responds to different stages in the drag lifecycle, such as drag start, entry, exit, drop, and end.
    private boolean onDragListener(View view, DragEvent event) {
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                // When a drag is initiated, save the original background color of the view if it hasn’t been saved yet.
                // This is done only once per view to avoid repeatedly overwriting the original color.
                if (!originalBackgroundColors.containsKey(view)) {
                    ColorDrawable background = (ColorDrawable) view.getBackground();
                    if (background != null) {
                        originalBackgroundColors.put(view, background.getColor());
                    }
                }
                // Ensure the dragged data has the correct MIME type; in this case, plain text.
                return event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN);

            case DragEvent.ACTION_DRAG_ENTERED:
                // When the dragged item enters the bounds of the view, highlight it with a gray color
                // to provide visual feedback indicating a possible drop target.
                view.setBackgroundColor(Color.GRAY);
                return true;

            case DragEvent.ACTION_DRAG_EXITED:
                // When the dragged item exits the view’s bounds, restore the view’s original background color.
                // This reverses the highlighting effect applied in the ACTION_DRAG_ENTERED stage.
                Integer originalColor = originalBackgroundColors.get(view);
                if (originalColor != null) {
                    view.setBackgroundColor(originalColor);
                }
                return true;

            case DragEvent.ACTION_DROP:
                // When the item is dropped, get the view being dragged from the event’s local state.
                View draggedView = (View) event.getLocalState();

                // Remove the dragged view from its original parent, if it has one.
                if (draggedView.getParent() != null) {
                    ((LinearLayout) draggedView.getParent()).removeView(draggedView);
                }

                // Add the dragged view to the target layout where it was dropped.
                LinearLayout targetLayout = (LinearLayout) view;
                targetLayout.addView(draggedView);

                // Adjust the layout parameters for the dragged view to wrap its content,
                // align it to the center of the target layout, and make it visible.
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) draggedView.getLayoutParams();
                params.width = LinearLayout.LayoutParams.WRAP_CONTENT;
                params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                params.gravity = android.view.Gravity.CENTER;
                draggedView.setLayoutParams(params);
                draggedView.setVisibility(View.VISIBLE);
                return true;

            case DragEvent.ACTION_DRAG_ENDED:
                // Once the drag action has ended (regardless of success or failure),
                // restore the original background color of the view to its pre-drag state.
                Integer endColor = originalBackgroundColors.get(view);
                if (endColor != null) {
                    view.setBackgroundColor(endColor);
                }
                return true;

            default:
                break;
        }
        return false;
    }


}
