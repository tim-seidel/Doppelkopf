package de.timseidel.doppelkopf.controller

import de.timseidel.doppelkopf.contracts.ISessionController

class DoppelkopfManager {

    companion object {
        private var controller: ISessionController = SessionController()

        fun getInstance() : ISessionController {
            return controller
        }

        fun setController(_controller: ISessionController){
            controller = _controller
        }
    }
}