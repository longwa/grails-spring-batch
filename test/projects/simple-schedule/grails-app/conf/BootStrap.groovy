import scheduling.Schedule

class BootStrap {

	Schedule schedule
	
    def init = { servletContext ->
		
		/*
		 * Let the scheduler know that the app is ready for processing 
		 */
		schedule.ready = true
    }
    def destroy = {
    }
}
