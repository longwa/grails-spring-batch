package scheduling

/**
 * Control the Schedule and see its status
 */
class BatchScheduleController {
	
	Schedule schedule
	
	def enable(){
		schedule.status = true
		redirect action:'status'
	}
	
	def disable(){
		schedule.status = false
		redirect action:'status'
	}
	
	def status(){
		render 'Schedule is ' + (schedule.isDisabled()?'disabled':'enabled')
	}
}
