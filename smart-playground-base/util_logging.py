
class Logger:
    @staticmethod
    def default():
        return StdLogger.get_instance()

    def log(self, msg):
        pass

    def info(self, msg):
        pass

    def input(self, msg):
        pass

    def config(self, msg):
        pass

    def debug(self, msg):
        pass

    def warning(self, msg):
        pass

    def err(self, msg):
        pass


INFO_PREFIX = '[ INFO ]  '
INPUT_PREFIX = '[ << ]    '
CONFIG_PREFIX = '[ OK ]    '
DEBUG_PREFIX = '[ @@@ ]   '
WARNING_PREFIX = '[ !!! ]   '
ERROR_PREFIX = '[ ERR ]   '


class StdLogger(Logger):
    __instance = None

    @staticmethod
    def get_instance():
        if StdLogger.__instance is None:
            StdLogger.__instance = StdLogger()
        return StdLogger.__instance

    def __init__(self):
        if StdLogger.__instance is not None:
            raise Exception('Cannot create multiple instances of a Singleton class')

    def log(self, msg):
        print(msg)

    def info(self, msg):
        print(INFO_PREFIX, msg)

    def input(self, msg):
        print(INPUT_PREFIX, msg)

    def config(self, msg):
        print(CONFIG_PREFIX, msg)

    def debug(self, msg):
        print(DEBUG_PREFIX, msg)

    def warning(self, msg):
        print(WARNING_PREFIX, msg)

    def err(self, msg):
        print(ERROR_PREFIX, msg)
