package com.example.contactsdemo.view.animation;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;

import com.example.contactsdemo.view.InOutImageButton;

public class ComposerButtonAnimation extends InOutAnimation {

	public static final int		DURATION	= 100;
	private static final int	xOffset		= 200;
	private static final int	yOffset		= 10;

	public ComposerButtonAnimation(Direction direction, long l, View view) {
		super(direction, l, new View[] { view });
	}

	public static void startAnimations(ViewGroup viewgroup,
			InOutAnimation.Direction direction) {
		switch (direction) {
		case IN:
			startAnimationsIn(viewgroup);
			break;
		case OUT:
			startAnimationsOut(viewgroup);
			break;
		}
	}

	private static void startAnimationsIn(ViewGroup viewgroup) {
		for (int i = 0; i < viewgroup.getChildCount(); i++) {
			if (viewgroup.getChildAt(i) instanceof InOutImageButton) {
				InOutImageButton inoutimagebutton = (InOutImageButton) viewgroup
						.getChildAt(i);
				ComposerButtonAnimation animation = new ComposerButtonAnimation(
						InOutAnimation.Direction.IN, DURATION, inoutimagebutton);
				animation.setStartOffset((i * 500)/ (-1 + viewgroup.getChildCount()));
				animation.setInterpolator(new OvershootInterpolator(2F));
				inoutimagebutton.startAnimation(animation);
			}
		}
	}

	private static void startAnimationsOut(ViewGroup viewgroup) {
		for (int i = 0; i < viewgroup.getChildCount(); i++) {
			if (viewgroup.getChildAt(i) instanceof InOutImageButton) {
				InOutImageButton inoutimagebutton = (InOutImageButton) viewgroup
						.getChildAt(i);
				ComposerButtonAnimation animation = new ComposerButtonAnimation(
						InOutAnimation.Direction.OUT, DURATION,
						inoutimagebutton);
				animation.setStartOffset((i * 50)/ (-1 + viewgroup.getChildCount()));
				animation.setInterpolator(new AnticipateInterpolator(2F));
				inoutimagebutton.startAnimation(animation);
			}
		}
	}

	@Override
	protected void addInAnimation(View[] aview) {
		Log.d("onScroll","addInAnimation-----");
		MarginLayoutParams mlp = (MarginLayoutParams) aview[0]
				.getLayoutParams();
		Log.d("OnScroll","mlp="+mlp);
		addAnimation(new TranslateAnimation(xOffset-mlp.leftMargin, 0F,
				-mlp.topMargin+yOffset, 0f));
	}

	@Override
	protected void addOutAnimation(View[] aview) {
		MarginLayoutParams mlp = (MarginLayoutParams) aview[0]
				.getLayoutParams();
		addAnimation(new TranslateAnimation(0F, xOffset -mlp.leftMargin, 0F,
				yOffset - mlp.topMargin));
	}
}

