DataSources:
	- JNDI
    - Memory Source With Persitence (binary format)
GUI:
	- Swing gui
		- Scheduling Editor
		- Control on Taks (Stop/start, etc.)
		- Tracking of Executed Tasks
	- Improve web gui
		- Scheduling Editor
		- Control on Taks (Stop/start, etc.)
		- Tracking of Executed Tasks
Events:
    - Generate events every time a task is executed
Distribution:
    - Prepare a the system to be able to run in multiple machines:
        - Centralized tasks administration, with information like this task should be run in those machines
        - One task running at the same time in multiple machines.
        - One task running in a pool of machines
            - Permiting policies of execution: round robin, etc.
        - unfinished tasks distribution, if a task isn't finished correctly in a machine others can restart it and conclude it
Integration:
	- JMX
    - JMS
    - Integrate with debian distribution
