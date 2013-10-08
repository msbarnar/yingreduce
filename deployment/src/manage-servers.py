__author__ = 'msbarnar@gmail.com'

# SSH Java update script
# Updates java via yum on remote servers over SSH

from fabric.api import cd, run, env, execute, parallel

username = 'msbarnar'
host_prefix = '149.169.30.'
hosts = range(9, 13)+range(35, 39)
numhosts = len(hosts)

env.hosts = [username+'@'+host_prefix + str(x) for x in hosts]
env.warn_only = True
env.skip_bad_hosts = True

def update_java():
    run('yum install java-1.7.0-openjdk')

def create_user():
    run('useradd msbarnar')
    run("passwd msbarnar")
    run('mkdir /home/msbarnar/.ssh')

@parallel
def git_clone():
    run('git config --global user.name "Matthew Barnard"')
    run('git config --global user.email "msbarnar@gmail.com"')
    run('git init mapreduce')

    with cd('mapreduce'):
        run('git remote add -f origin https://github.com/msbarnar/yingreduce/')
        run('git config core.sparsecheckout true')
        run('echo deployment/target/ >> .git/info/sparse-checkout')
        run('git pull origin master')

    run('ln -s mapreduce/deployment/target/deployment-1.0-SNAPSHOT.jar mapreduce.jar')

@parallel
def git_pull():
    with cd('mapreduce'):
        run('git pull origin master')

@parallel
def run_client():
    run('java -jar mapreduce.jar')

def set_numhosts():
    try:
        num = int(raw_input('# of hosts: '))
    except ValueError:
        return
    global numhosts
    numhosts = max(num, 1)
    numhosts = min(num, len(hosts))
    env.hosts = [username+'@'+host_prefix + str(x) for x in hosts[:numhosts]]

if __name__ == '__main__':
    selection = ''
    options = {'1': git_pull, '2': run_client, '7': git_clone,
               '8': create_user, '9': update_java}

    while selection != 'q':
        print '-------Ying Server Cluster---------'
        print 'User:\t' + username
        print 'Hosts:\t' + host_prefix + str(hosts[:numhosts])
        print ''
        print '0) Set number of hosts to target'
        print '1) Update git repository'
        print '2) Run client'
        print '7) Init git repository'
        print '8) Add user "msbarnar"'
        print '9) Update jdk to java-1.7.0-openjdk'
        print '-----------------------------------'
        selection = raw_input('(q to exit) ? ')

        if selection == '0':
            set_numhosts()
        else:
            try:
                execute(options[selection])
            except KeyError:
                pass

