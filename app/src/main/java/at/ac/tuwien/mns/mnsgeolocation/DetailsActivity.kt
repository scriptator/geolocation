package at.ac.tuwien.mns.mnsgeolocation

import android.os.Bundle
import android.app.Activity
import android.net.Uri
import at.ac.tuwien.mns.mnsgeolocation.dto.Measurement
import at.ac.tuwien.mns.mnsgeolocation.fragments.DetailsFragment


/**
 * Created by domin on 05.01.2018.
 */
class DetailsActivity : Activity(), DetailsFragment.OnFragmentInteractionListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            val details = DetailsFragment.newInstance(intent.extras.getParcelable(DetailsFragment.ARG_MEASUREMENT))
            fragmentManager.beginTransaction().add(android.R.id.content, details).commit()
        }
    }

    override fun onFragmentInteraction(uri: Uri) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}