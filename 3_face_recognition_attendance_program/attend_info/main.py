import tkinter as tk
import ui_init as ui
import make_widgets as widgets
import info_dao as dao
import info_service as service

if __name__ == '__main__':
    logo = 'logo.png'
    root = tk.Tk()
    app = ui.AppWindow(root, logo)
    dao = dao.InfoDao()
    service = service.InfoService(dao)
    widgets.make(app, service)
    app.mainloop()
