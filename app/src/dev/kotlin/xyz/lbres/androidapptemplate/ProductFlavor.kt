package xyz.lbres.androidapptemplate

import xyz.lbres.androidapptemplate.ext.view.visible
import xyz.lbres.androidapptemplate.ui.BaseFragment
import xyz.lbres.androidapptemplate.ui.devtools.DeveloperToolsDialog

/**
 * Configuration for dev build flavor
 */
object ProductFlavor : ProductFlavorConfig {
    override val devMode = true

    /**
     * Show dev tools button and setup dialog
     */
    override fun setupFlavor(activity: BaseActivity) {
        val devToolsButton = activity.binding.devToolsButton
        devToolsButton.visible()

        val dialog = DeveloperToolsDialog()
        devToolsButton.setOnClickListener {
            val fragmentManager = BaseFragment.dialogFragmentManager

            if (fragmentManager != null) {
                dialog.show(fragmentManager, DeveloperToolsDialog.TAG)
            }
        }
    }
}
