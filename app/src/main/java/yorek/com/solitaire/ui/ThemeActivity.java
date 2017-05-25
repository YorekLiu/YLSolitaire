package yorek.com.solitaire.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import yorek.com.solitaire.GlobalApplication;
import yorek.com.solitaire.R;
import yorek.com.solitaire.bean.Card;

public class ThemeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme);

        ListView themeListView = (ListView) findViewById(R.id.theme_list);
        themeListView.setAdapter(new ThemeAdapter());

        themeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GlobalApplication.setCardSkin(position);
                ThemeActivity.this.setResult(Activity.RESULT_OK);
                ThemeActivity.this.finish();
            }
        });
    }

    private class ThemeAdapter extends BaseAdapter {
        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public int getCount() {
            return GlobalApplication.CARD_TOTAL_SKIN;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(ThemeActivity.this).inflate(R.layout.item_list_theme, null);
                viewHolder = new ViewHolder();
                viewHolder.imageView1 = (ImageView) convertView.findViewById(R.id.iv_club);
                viewHolder.imageView2 = (ImageView) convertView.findViewById(R.id.iv_diamond);
                viewHolder.imageView3 = (ImageView) convertView.findViewById(R.id.iv_heart);
                viewHolder.imageView4 = (ImageView) convertView.findViewById(R.id.iv_spade);
                viewHolder.imageView5 = (ImageView) convertView.findViewById(R.id.iv_card_back);
                viewHolder.selectedTextView = (TextView) convertView.findViewById(R.id.theme_select);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            String imgResPrefix = GlobalApplication.getSkinPrefix(position);
            viewHolder.imageView1.setImageResource(getResources().getIdentifier(
                    imgResPrefix + "club1", "drawable", getPackageName()));
            viewHolder.imageView2.setImageResource(getResources().getIdentifier(
                    imgResPrefix + "diamond1", "drawable", getPackageName()));
            viewHolder.imageView3.setImageResource(getResources().getIdentifier(
                    imgResPrefix + "heart1", "drawable", getPackageName()));
            viewHolder.imageView4.setImageResource(getResources().getIdentifier(
                    imgResPrefix + "spade1", "drawable", getPackageName()));
            viewHolder.imageView5.setImageResource(getResources().getIdentifier(
                    Card.sCardBackName + (position / 2 + 1), "drawable", getPackageName()));
            viewHolder.selectedTextView.setVisibility(position == GlobalApplication.getCardSkin() ? View.VISIBLE : View.GONE);

            return convertView;
        }
    }

    private static class ViewHolder {
        private ImageView imageView1;
        private ImageView imageView2;
        private ImageView imageView3;
        private ImageView imageView4;
        private ImageView imageView5;
        private TextView selectedTextView;
    }
}
