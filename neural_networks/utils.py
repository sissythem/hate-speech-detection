import time, smtplib
from email.mime.text import MIMEText


def elapsed_str(previous_tic, up_to=None):
    """
    Calculate the time difference between up_to and previous_tic
    :param previous_tic: a previous datetime
    :param up_to: time-end (either current time or a specified datetime
    :return: the difference between the two params
    """
    if up_to is None:
        up_to = time.time()
    duration_sec = up_to - previous_tic
    m, s = divmod(duration_sec, 60)
    h, m = divmod(m, 60)
    return "%d:%02d:%02d" % (h, m, s)


# datetime for timestamps
def get_datetime():
    """
    Get current datetime
    :return: current datetime
    """
    return time.time()


def send_email(config_email, time_needed):
    """
    Function to send email - notify when program is finished
    :param config_email: dictionary with email config
    :param time_needed: difference between time-end and time-start
    :return:
    """
    message = config_email["message"] + " Time needed: " + time_needed
    msg = MIMEText(message)
    smtp = smtplib.SMTP('smtp.gmail.com:587')
    smtp.starttls()
    smtp.login(config_email["sender_email"], config_email["sender_pass"])
    smtp.sendmail(config_email["sender_email"], config_email["recipient"], msg)
    smtp.close()
