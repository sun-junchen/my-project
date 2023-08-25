<template>
  <div style="width: 100vw; height: 100vh; overflow: hidden; display: flex">
    <div class="welcome-title"></div>
    <div class="right-card">
      <router-view />
    </div>
  </div>
</template>

<script>
export default {
  name: 'WelcomeView'
}
</script>

<style scoped>
.right-card {
  align-items: center;
  margin: auto;
  width: 400px;
  z-index: 1;
  background-color: white;
  height: 10000px;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
}

.welcome-title {
  position: absolute;
  bottom: 30px;
  left: 30px;
  color: white;
  text-shadow: 0 0 10px black;
}

div {
  background-image: url('../assets/1.jpg');
  /* 背景图垂直、水平均居中 */
  background-position: center center;
  /* 背景图不平铺 */
  background-repeat: no-repeat;
  /* 当内容高度大于图片高度时，背景图像的位置相对于viewport固定 */
  background-attachment: fixed;
  /* 让背景图基于容器大小伸缩 */
  background-size: cover;
  /* 设置背景颜色，背景图加载过程中会显示背景色 */
  background-color: #464646;
}
</style>
