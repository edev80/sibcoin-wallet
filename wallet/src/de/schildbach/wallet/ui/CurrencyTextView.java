/*
 * Copyright 2013-2015 the original author or authors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.schildbach.wallet.ui;

import org.bitcoinj.core.Monetary;
import org.bitcoinj.utils.MonetaryFormat;

import de.schildbach.wallet.Constants;
import de.schildbach.wallet.util.GenericUtils;
import de.schildbach.wallet.util.MonetarySpannable;
import de.schildbach.wallet_test.R;
//import rusapps.sibcoin.wallet.R;

import android.content.Context;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.ScaleXSpan;
import android.util.AttributeSet;
import android.graphics.drawable.Drawable;

/**
 * @author Andreas Schildbach
 */
public class CurrencyTextView extends AppCompatTextView {
    private Monetary amount = null;
    private MonetaryFormat format = null;
    private boolean alwaysSigned = false;
    private RelativeSizeSpan prefixRelativeSizeSpan = null;
    private ScaleXSpan prefixScaleXSpan = null;
    private ForegroundColorSpan prefixColorSpan = null;
    private RelativeSizeSpan insignificantRelativeSizeSpan = null;
    private boolean isCodePositionPrefix = true;
    private Drawable currencySymbolDrawable;

    public CurrencyTextView(final Context context) {
        super(context);
    }

    public CurrencyTextView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public void setAmount(final Monetary amount) {
        this.amount = amount;
        updateView();
    }

	public void setFormat(final MonetaryFormat format) {
		if (format.code() != null) {
			// Special case for SIB
			if (format.code().equalsIgnoreCase(MonetaryFormat.CODE_BTC)) {
				currencySymbolDrawable = getResources().getDrawable(R.drawable.currency_symbol_btc);
			} else if (format.code().equalsIgnoreCase(MonetaryFormat.CODE_MBTC)) {
				currencySymbolDrawable = getResources().getDrawable(R.drawable.currency_symbol_mbtc);
			} else if (format.code().equalsIgnoreCase(MonetaryFormat.CODE_UBTC)) {
				currencySymbolDrawable = getResources().getDrawable(R.drawable.currency_symbol_ubtc);
			} else if (format.code().equalsIgnoreCase(Constants.PREFIX_ALMOST_EQUAL_TO + "rub") ||
			           format.code().equalsIgnoreCase(Constants.PREFIX_ALMOST_EQUAL_TO + "rur")) {

				final String localCurrencyCode = format.code().substring(2); // remove ~ sign

				final String currencySymbol = GenericUtils.currencySymbol(localCurrencyCode);
				final float textSize = getTextSize();
//				final float smallerTextSize = textSize * (20f / 24f);
				final float smallerTextSize = textSize * (24f / 24f);

				// Don't draw code as compound drawable
				isCodePositionPrefix = false;
				currencySymbolDrawable = new CurrencySymbolDrawable(currencySymbol, smallerTextSize,
				                                                    getResources().getColor(R.color.fg_less_significant),
				                                                    textSize * 0.37f);

//				this.format = Constants.LOCAL_FORMAT.code(0, Constants.PREFIX_ALMOST_EQUAL_TO);
//				currencyTextView.setText(currencySymbol);
//				currencyTextView.setVisibility(VISIBLE);
			} else {
				currencySymbolDrawable = null;
			}
		}

		this.format = format.codeSeparator(Constants.CHAR_HAIR_SPACE);
		updateView();
	}

    public void setAlwaysSigned(final boolean alwaysSigned) {
        this.alwaysSigned = alwaysSigned;
        updateView();
    }

    public void setStrikeThru(final boolean strikeThru) {
        if (strikeThru)
            setPaintFlags(getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        else
            setPaintFlags(getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
    }

    public void setInsignificantRelativeSize(final float insignificantRelativeSize) {
        if (insignificantRelativeSize != 1) {
            this.prefixRelativeSizeSpan = new RelativeSizeSpan(insignificantRelativeSize);
            this.insignificantRelativeSizeSpan = new RelativeSizeSpan(insignificantRelativeSize);
        } else {
            this.prefixRelativeSizeSpan = null;
            this.insignificantRelativeSizeSpan = null;
        }
    }

    public void setPrefixColor(final int prefixColor) {
        this.prefixColorSpan = new ForegroundColorSpan(prefixColor);
        updateView();
    }

    public void setPrefixScaleX(final float prefixScaleX) {
        this.prefixScaleXSpan = new ScaleXSpan(prefixScaleX);
        updateView();
    }

    public void setCodePosition(final boolean isPrefix) {
        isCodePositionPrefix = isPrefix;
        updateView();
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        setPrefixColor(getResources().getColor(R.color.fg_less_significant));
        setPrefixScaleX(1);
        setInsignificantRelativeSize(0.85f);
        setSingleLine();
    }

	private void updateView() {
		final MonetarySpannable text;

		if (amount != null) {

			// draw currency code drawable instead of its text representation
			if (currencySymbolDrawable != null) {
				setCompoundDrawablePadding(5);
				format = format.noCode();
			}
			text = new MonetarySpannable(format, alwaysSigned, amount)
			         .applyMarkup(new Object[]{prefixRelativeSizeSpan, prefixScaleXSpan, prefixColorSpan},
			                      new Object[]{insignificantRelativeSizeSpan});

			if (isCodePositionPrefix) {
				setCompoundDrawablesWithIntrinsicBounds(currencySymbolDrawable, null, null, null);
			} else {
				setCompoundDrawablesWithIntrinsicBounds(null, null, currencySymbolDrawable, null);
			}

		} else {
			text = null;
		}

		setText(text);
	}
}
