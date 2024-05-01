# java-kanban
Добрый вечер.

1. "По логике: эпики и сабтаски удаляются валидно в соответствии с тз." 
	- это замечание или вопрос? Или констатация факта? Если вопрос то можно пожалуйста подробнее, что переделать?

2. "По логике есть вопрос. Оставил его в комментариях.", "Какой смысл инициализировать переменную codes если она потом используется только один раз?"
	- в данном случае никакого по-скольку список мы действительно получим единожды и единожды используем. Иногда я создаю переменные для того, что бы избежать проблем с читабельностью кода.

	Аналогичный вопрос может возникнуть к TaskManager::refreshStatus
	
	В реализации:
	```
	ArrayList<TaskStatuses> statuses = getSubtaskStatusesList(epic);
	TaskStatuses epicStatus = calculateEpicStatus(statuses);
	updateEpic(new Epic(epic.getTitle(), epic.getDescription(), epic.getId(), epicStatus, epic.getSubtaskCodes()));
	```
	
	Пример как могло бы быть:
	```
	updateEpic(new Epic(epic.getTitle(), epic.getDescription(), epic.getId(), calculateEpicStatus(getSubtaskStatusesList(epic)), epic.getSubtaskCodes()));
	```

	Я не думаю, что в конкретном случае это слишком ресурсозатратно, но исправлю.
