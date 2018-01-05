package at.ac.tuwien.mns.mnsgeolocation

import android.os.Bundle
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import at.ac.tuwien.mns.mnsgeolocation.fragments.DetailsFragment
import android.view.MenuItem
import android.widget.Toast


/**
 * Created by domin on 05.01.2018.
 */
class DetailsActivity : AppCompatActivity(), DetailsFragment.OnFragmentInteractionListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        val details = DetailsFragment.newInstance(intent.extras.getParcelable(DetailsFragment.ARG_MEASUREMENT))
        fragmentManager.beginTransaction().replace(R.id.detail, details).commit()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_details, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_delete -> {
                Toast.makeText(this, "Delete selected", Toast.LENGTH_SHORT).show()
                return true
            }
            R.id.action_mail -> {
                Toast.makeText(this, "Mail selected", Toast.LENGTH_SHORT).show()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
        return false
    }

    override fun onFragmentInteraction(uri: Uri) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}