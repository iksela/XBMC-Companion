package net.iksela.xbmc.companion;

import java.util.List;

import net.iksela.xbmc.companion.data.Actor;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CastAdapter extends ArrayAdapter<Actor> {
	
	private Context context;
	private int resourceId;
	List<Actor> actors;
	
	static class ActorHolder {
		TextView name;
		TextView role;
	}

	public CastAdapter(Context context, int textViewResourceId, List<Actor> objects) {
		super(context, textViewResourceId, objects);
		this.context = context;
		this.resourceId = textViewResourceId;
		this.actors = objects;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		//return super.getView(position, convertView, parent);
		ActorHolder holder = null;
		
		if (convertView == null) {
			convertView = ((Activity)this.context).getLayoutInflater().inflate(this.resourceId, parent, false);
			holder = new ActorHolder();
			holder.name = (TextView)convertView.findViewById(R.id.textViewActorName);
			holder.role = (TextView)convertView.findViewById(R.id.textViewActorRole);
			
			convertView.setTag(holder);
		}
		else {
			holder = (ActorHolder) convertView.getTag();
		}
		
		Actor actor = this.actors.get(position);
		holder.name.setText(actor.getName());
		holder.role.setText(actor.getRole());
		
		return convertView;
	}

}
