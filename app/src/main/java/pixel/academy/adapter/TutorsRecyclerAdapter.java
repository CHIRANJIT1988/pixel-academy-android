package pixel.academy.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.List;

import pixel.academy.R;
import pixel.academy.helper.Blur;
import pixel.academy.helper.Helper;
import pixel.academy.helper.OnTaskCompleted;
import pixel.academy.model.Tutor;

import static pixel.academy.app.MyApplication.getInstance;


public class TutorsRecyclerAdapter extends RecyclerView.Adapter<TutorsRecyclerAdapter.VersionViewHolder>
{

    private List<Tutor> tutorList;

    private Context context;
    private OnTaskCompleted listener;
    private OnItemClickListener clickListener;


    public TutorsRecyclerAdapter(Context context, OnTaskCompleted listener, List<Tutor> tutorList)
    {
        this.context = context;
        this.listener = listener;
        this.tutorList = tutorList;
    }


    @Override
    public VersionViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
    {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerlist_item_tutor, viewGroup, false);
        return new VersionViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final VersionViewHolder versionViewHolder, int i)
    {

        Tutor tutor = tutorList.get(i);

        versionViewHolder.tutor_name.setText(Helper.toCamelCase(tutor.first_name + " " + tutor.last_name));

        /*StringBuilder edu =  new StringBuilder();

        for(Education education: tutor.educationList)
        {
            edu.append(education.qualification).append(", ");
        }*/

        versionViewHolder.location.setText(Helper.toCamelCase(tutor.addressObj.city + ", " + tutor.addressObj.country) + " - " + tutor.addressObj.pincode);
        versionViewHolder.fees_range.setText(tutor.preferenceObj.fees_range);
        versionViewHolder.day.setText(String.valueOf(tutor.preferenceObj.days_per_week));
        versionViewHolder.hour.setText(String.valueOf(tutor.preferenceObj.duration_per_day));


        Transformation blurTransformation = new Transformation() {

            @Override
            public Bitmap transform(Bitmap source) {
                Bitmap blurred = Blur.fastblur(context, source, 10);
                source.recycle();
                return blurred;
            }

            @Override
            public String key() {
                return "blur()";
            }
        };

        /**
         * Profile Picture target URL
         */
        final String URL = getInstance().getResources().getString(R.string.pixelServerBaseUrl)
                + getInstance().getResources().getString(R.string.pixelServerProfilePicUrl) + tutor.profile_pic;

        Log.v("URL", URL);

        Picasso.with(context)
                .load(URL)
                .resize(280, 280)
                .transform(blurTransformation)
                .into(versionViewHolder.ivImage, new Callback() {

                    @Override
                    public void onSuccess()
                    {
                        Picasso.with(context)
                                .load(URL) // image url goes here
                                .into(versionViewHolder.ivImage);
                    }

                    @Override
                    public void onError() {

                    }
                });

        versionViewHolder.btnPing.setTag(i);
        versionViewHolder.btnCall.setTag(i);
        versionViewHolder.btnFavourite.setTag(i);
    }


    @Override
    public int getItemCount()
    {
        return tutorList == null ? 0 : tutorList.size();
    }


    class VersionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        TextView tutor_name;
        TextView location;
        TextView fees_range;
        TextView day;
        TextView hour;
        ImageView ivImage;
        Button btnPing;
        Button btnCall;
        Button btnFavourite;


        public VersionViewHolder(View itemView)
        {

            super(itemView);

            tutor_name = (TextView) itemView.findViewById(R.id.tutor_name);
            location = (TextView) itemView.findViewById(R.id.location);
            fees_range = (TextView) itemView.findViewById(R.id.fees_range);
            day = (TextView) itemView.findViewById(R.id.day);
            hour = (TextView) itemView.findViewById(R.id.hour);
            ivImage = (ImageView) itemView.findViewById(R.id.profile_image);
            btnPing = (Button) itemView.findViewById(R.id.btnPing);
            btnCall = (Button) itemView.findViewById(R.id.btnCall);
            btnFavourite = (Button) itemView.findViewById(R.id.btnFavourite);

            btnPing.setOnClickListener(onButtonClickListener);
            btnCall.setOnClickListener(onButtonClickListener);
            btnFavourite.setOnClickListener(onButtonClickListener);
            itemView.setOnClickListener(this);
        }


        private View.OnClickListener onButtonClickListener = new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {

                if (v.getId() == R.id.btnPing)
                {
                    listener.onTaskCompleted(true, (int) v.getTag(), "ping");
                }

                else if (v.getId() == R.id.btnCall)
                {

                    listener.onTaskCompleted(true, (int) v.getTag(), "call");
                }

                else if (v.getId() == R.id.btnFavourite)
                {

                    listener.onTaskCompleted(true, (int) v.getTag(), "fav");
                }
            }
        };


        @Override
        public void onClick(View v)
        {
            clickListener.onItemClick(v, getAdapterPosition());
        }
    }


    public interface OnItemClickListener
    {
        void onItemClick(View view, int position);
    }


    public void SetOnItemClickListener(final OnItemClickListener itemClickListener)
    {
        this.clickListener = itemClickListener;
    }


    /*private Bitmap getCircleBitmap(Bitmap bitmap)
    {

        final Bitmap output = Bitmap.createBitmap(140, 140, Bitmap.Config.ARGB_8888);

        final Canvas canvas = new Canvas(output);

        final int color = Color.RED;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, 140, 140);
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        bitmap.recycle();

        return output;
    }*/
}