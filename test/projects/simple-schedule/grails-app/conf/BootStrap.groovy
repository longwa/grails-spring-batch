import scheduling.Schedule

class BootStrap {

	Schedule schedule
	
    def init = { servletContext ->
		schedule.ready = true
    }
    def destroy = {
    }
}
