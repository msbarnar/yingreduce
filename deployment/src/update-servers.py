__author__ = 'msbarnar@gmail.com'

# SSH Java update script
# Updates java via yum on remote servers over SSH

from fabric.api import cd, run, env, execute, parallel

env.hosts = ['msbarnar@149.169.30.' + str(x) for x in range(9, 13)+range(35, 39)]
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

if __name__ == '__main__':
    print '1) Update git repository'
    print '7) Init git repository'
    print '8) Add user "msbarnar"'
    print '9) Update jdk to java-1.7.0-openjdk'

    selection = raw_input('? ')

    if selection == '1':
        execute(git_pull)
    elif selection == '7':
        execute(git_clone)
    elif selection == '8':
        execute(create_user)
    elif selection == '9':
        execute(update_java)
    else:
        exit(0)

