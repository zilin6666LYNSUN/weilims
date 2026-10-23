<template>
  <div class="login">
    <img src="../assets/images/image2.png" class="background-img">
    <div class="left-box">

      <div class="left-bottom">
        <h2>{{ this.$t('login.brand') }}</h2>
        <h3>{{ this.$t('login.welcome') }}</h3>
        <h6>{{ this.$t('login.subtitle') }}</h6>
      </div>
      <img src="../assets/images/image.png" class="left-top" />
    </div>
    <lang-select class="login-lang" />
    <div class="right-box">
      <el-form ref="loginForm" :model="loginForm" :rules="loginRules" class="login-form">
        <h2 class="title">{{ this.$t('login.title') }}</h2>
        <el-form-item prop="username">
          <el-input v-model="loginForm.username" type="text" auto-complete="off" :placeholder="$t('login.username')">
            <svg-icon slot="prefix" icon-class="user" class="el-input__icon input-icon" />
          </el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="loginForm.password" type="password" auto-complete="off" :placeholder="$t('login.password')"
            @keyup.enter.native="handleLogin">
            <svg-icon slot="prefix" icon-class="password" class="el-input__icon input-icon" />
          </el-input>
        </el-form-item>
        <el-checkbox v-model="loginForm.rememberMe" style="margin:0px 0px 25px 0px;">{{ this.$t('login.remember') }}</el-checkbox>
        <el-form-item style="width:100%;">
          <el-button :loading="loading" size="medium" type="primary" style="width:100%;"
            @click.native.prevent="handleLogin">
            <span v-if="!loading">{{ this.$t('login.signin') }}</span>
            <span v-else>{{ this.$t('login.signingIn') }}</span>
          </el-button>
          <div style="float: right;" v-if="register">
            <router-link class="link-type" :to="'/register'">{{ this.$t('login.register') }}</router-link>
          </div>
        </el-form-item>
      </el-form>
    </div>

    <!--  底部  -->
    <div class="el-login-footer">
      <span @click="toIPC">京ICP备2024079399号</span>
    </div>
  </div>
</template>

<script>
import Cookies from "js-cookie";
import { encrypt, decrypt } from '@/utils/jsencrypt'
import LangSelect from "@/components/LangSelect";

export default {
  name: "Login",
  components: { LangSelect },
  data() {
    return {
      loginForm: {
        username: "kf1",
        password: "123456",
        rememberMe: false,
        code: "",
        uuid: ""
      },
      loginRules: {
        username: [
          { required: true, trigger: "blur", message: this.$t('login.validateUsername') }
        ],
        password: [
          { required: true, trigger: "blur", message: this.$t('login.validatePassword') }
        ]
      },
      loading: false,
      // 注册开关
      register: false,
      redirect: undefined
    };
  },
  watch: {
    $route: {
      handler: function (route) {
        this.redirect = route.query && route.query.redirect;
      },
      immediate: true
    }
  },
  created() {
    this.getCookie();
  },
  methods: {
    toIPC() {
      window.open("https://beian.miit.gov.cn/", "_blank");
    },
    getCookie() {
      const username = Cookies.get("username");
      const password = Cookies.get("password");
      const rememberMe = Cookies.get('rememberMe')
      this.loginForm = {
        username: username === undefined ? this.loginForm.username : username,
        password: password === undefined ? this.loginForm.password : decrypt(password),
        rememberMe: rememberMe === undefined ? false : Boolean(rememberMe)
      };
    },
    handleLogin() {
      this.$refs.loginForm.validate(valid => {
        if (valid) {
          this.loading = true;
          if (this.loginForm.rememberMe) {
            Cookies.set("username", this.loginForm.username, { expires: 30 });
            Cookies.set("password", encrypt(this.loginForm.password), { expires: 30 });
            Cookies.set('rememberMe', this.loginForm.rememberMe, { expires: 30 });
          } else {
            Cookies.remove("username");
            Cookies.remove("password");
            Cookies.remove('rememberMe');
          }
          this.$store.dispatch("Login", this.loginForm).then(() => {
            // 加载单位
            this.$store.dispatch('getUnitmeasureList');
            this.$router.push({ path: this.redirect || "/" }).catch(() => { });
          }).catch(() => {
            this.loading = false;
          });
        }
      });
    }
  }
};
</script>

<style rel="stylesheet/scss" lang="scss">
.login {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  background: #4285F4;

  .background-img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    position: absolute;
  }
}

.left-box {
  width: 650px;
  height: 650px;
  position: relative;
  left: -10px;
  margin-bottom: 15px;
  text-align: center;

  .left-top {
    width: 70%;
    display: inline-block;
    margin: 20px 0 0 -140px
  }

  .left-bottom {
    // background-color: #1e5e3e;
    width: 500px;
    color: #ffffff;
    text-align: left;
    margin: 80px 0 0 20px;
  }
}

.right-box {
  width: 440px;
  height: 460px;
  background: #ffffff;
  position: relative;
  left: -10px;
  margin-bottom: 15px;
  // text-align: center;
  display: grid;
  place-items: center;
  border-radius: 40px 40px 40px 40px;
}

.title {
  margin: -25px auto 30px auto;
  text-align: center;
  color: #181818;
}

.login-lang {
  position: absolute;
  top: 16px;
  right: 32px;
  z-index: 10;
}

.login-lang .lang-select {
  display: inline-flex;
  align-items: center;
  height: 40px;
  padding: 0 14px;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.92);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  cursor: pointer;
  transition: all 0.2s ease;
}

.login-lang .lang-select:hover {
  background: #ffffff;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

.login-lang .lang-icon {
  font-size: 18px;
  margin-right: 6px;
}

.login-lang .lang-label {
  font-size: 14px;
  font-weight: 600;
  color: #333;
  max-width: none;
  overflow: visible;
  text-overflow: unset;
  white-space: nowrap;
}

.login-lang .lang-caret {
  font-size: 14px;
  margin-left: 6px;
  color: #333;
}

.login-form {
  border-radius: 8px;
  background: #ffffff;
  width: 400px;
  padding: 25px 25px 5px 25px;

  .el-input {
    height: 38px;

    input {
      height: 38px;
    }
  }

  .input-icon {
    height: 39px;
    width: 14px;
    margin-left: 2px;
  }
}

.login-tip {
  font-size: 13px;
  text-align: center;
  color: #bfbfbf;
}

.el-login-footer {
  height: 40px;
  line-height: 40px;
  position: fixed;
  bottom: 0;
  width: 100%;
  text-align: center;
  color: #e9e4e4;
  font-family: Arial;
  font-size: 10px;
  letter-spacing: 1px;
  margin-bottom: -4px;
}
</style>
