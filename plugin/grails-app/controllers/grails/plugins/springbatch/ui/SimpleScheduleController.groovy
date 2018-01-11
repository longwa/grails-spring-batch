package grails.plugins.springbatch.ui

import grails.plugins.springbatch.SimpleScheduleService

/**
 * Control the SimpleSchedule and see its status
 */
class SimpleScheduleController {
	
	SimpleScheduleService simpleScheduleService
	
	def enable(){
		simpleScheduleService.status = true
		redirect action:'status'
	}
	
	def disable(){
		simpleScheduleService.status = false
		redirect action:'status'
	}
	
	def status(){
		render 'Schedule is ' + (simpleScheduleService.isDisabled() ? 'disabled':'enabled')
	}
}
